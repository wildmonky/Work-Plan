import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_104ea3beb114536190d8c9d366189567
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:pwSendElinkOnce('" + mapRow.get("ID")+ "')";
return url;

    }
}
