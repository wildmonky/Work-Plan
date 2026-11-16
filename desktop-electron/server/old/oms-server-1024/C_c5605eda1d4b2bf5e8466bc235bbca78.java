import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_c5605eda1d4b2bf5e8466bc235bbca78
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:sendSms('" + mapRow.get("ID")+ "')";
return url;

    }
}
