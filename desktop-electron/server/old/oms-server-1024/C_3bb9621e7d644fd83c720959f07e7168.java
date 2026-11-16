import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_3bb9621e7d644fd83c720959f07e7168
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:callOnce('" + mapRow.get("ID")+ "')";
return url;

    }
}
