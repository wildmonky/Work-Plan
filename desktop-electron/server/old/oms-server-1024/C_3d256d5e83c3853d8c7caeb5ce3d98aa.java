import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_3d256d5e83c3853d8c7caeb5ce3d98aa
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:sendElink('" + mapRow.get("LOG_ID")+ "')";
return url;

    }
}
